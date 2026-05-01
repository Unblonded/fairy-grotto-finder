package unblonded.fullbright.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import unblonded.fullbright.Config;
import unblonded.fullbright.Fullbright;
import unblonded.fullbright.util.Color;
import unblonded.fullbright.util.PosColor;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class RenderCallback {
    private static final List<PosColor> renderQueue = new CopyOnWriteArrayList<>();
    private static final List<PosColor> tracerQueue = new CopyOnWriteArrayList<>();

    public static Color activeColor = new Color("#ff08da");

    public static void setActiveColor(Color color) {
        activeColor = color;
    }

    public static void addToQueue(PosColor block) {
        for (PosColor pc : renderQueue) {
            if (pc.pos == block.pos) {
                return;
            }
        }

        renderQueue.add(block);
    }

    public static int queueSize() {
        return ((renderQueue.size() + tracerQueue.size()) / 2);
    }

    public static void clearQueue() {
        renderQueue.clear();
    }

    public static void renderBlockOutline() {
        if (renderQueue.isEmpty()) return;
        if (!Config.showOutlines.get()) return;
        if (MinecraftClient.getInstance().player == null) return;

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(
                RenderPipelines.LINES.getVertexFormatMode(),
                RenderPipelines.LINES.getVertexFormat()
        );

        // Emit every block outline into the same buffer
        for (PosColor pc : renderQueue) {
            float x0 = pc.pos.getX(), y0 = pc.pos.getY(), z0 = pc.pos.getZ();
            float x1 = x0 + 1f,      y1 = y0 + 1f,      z1 = z0 + 1f;

            //float r = pc.R(), g = pc.G(), b = pc.B(), a = pc.A();
            float r = activeColor.R(), g = activeColor.G(), b = activeColor.B(), a = activeColor.A();

            // bottom face
            addLine(bufferBuilder, x0,y0,z0, x1,y0,z0, r,g,b,a);
            addLine(bufferBuilder, x1,y0,z0, x1,y0,z1, r,g,b,a);
            addLine(bufferBuilder, x1,y0,z1, x0,y0,z1, r,g,b,a);
            addLine(bufferBuilder, x0,y0,z1, x0,y0,z0, r,g,b,a);
            // top face
            addLine(bufferBuilder, x0,y1,z0, x1,y1,z0, r,g,b,a);
            addLine(bufferBuilder, x1,y1,z0, x1,y1,z1, r,g,b,a);
            addLine(bufferBuilder, x1,y1,z1, x0,y1,z1, r,g,b,a);
            addLine(bufferBuilder, x0,y1,z1, x0,y1,z0, r,g,b,a);
            // verticals
            addLine(bufferBuilder, x0,y0,z0, x0,y1,z0, r,g,b,a);
            addLine(bufferBuilder, x1,y0,z0, x1,y1,z0, r,g,b,a);
            addLine(bufferBuilder, x1,y0,z1, x1,y1,z1, r,g,b,a);
            addLine(bufferBuilder, x0,y0,z1, x0,y1,z1, r,g,b,a);
        }

        BuiltBuffer builtBuffer = bufferBuilder.endNullable();
        if (builtBuffer == null) return;

        try {
            int indexCount = builtBuffer.getDrawParameters().indexCount();
            GpuBuffer vertexBuffer = RenderSystem.getDevice()
                    .createBuffer(() -> "outline vertex buffer", GpuBuffer.USAGE_VERTEX, builtBuffer.getBuffer());

            Vec3d camPos = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            GpuTextureView colorView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
            GpuTextureView depthView = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();

            matrix4fStack.pushMatrix();
            matrix4fStack.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);

            GpuBufferSlice[] uniforms = RenderSystem.getDynamicUniforms().writeTransforms(
                    new DynamicUniforms.TransformsValue(
                            new Matrix4f(matrix4fStack),
                            new Vector4f(1f, 1f, 1f, 1f),
                            new Vector3f(),
                            new Matrix4f()
                    )
            );

            RenderSystem.setShaderFog(uniforms[0]);
            GpuBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.LINES).getIndexBuffer(indexCount);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            try (RenderPass renderPass = RenderSystem.getDevice()
                    .createCommandEncoder()
                    .createRenderPass(() -> "outline", colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {

                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.setIndexBuffer(indexBuffer, RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.LINES).getIndexType());
                renderPass.setUniform("DynamicTransforms", uniforms[0]);
                renderPass.setPipeline(RenderPipelines.LINES);
                renderPass.drawIndexed(0, 0, indexCount, 1);  // single draw for ALL blocks
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            matrix4fStack.popMatrix();
            vertexBuffer.close();
            builtBuffer.close();
        } catch (Exception e) {
            builtBuffer.close();
        }
    }

    public static void renderGlow() {
        if (renderQueue.isEmpty()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_COLOR
        );

        for (PosColor pc : renderQueue) {
            float x0 = pc.pos.getX(), y0 = pc.pos.getY(), z0 = pc.pos.getZ();
            float x1 = x0 + 1f, y1 = y0 + 1f, z1 = z0 + 1f;
            //float r = pc.R(), g = pc.G(), b = pc.B(), a = pc.A();
            float r = activeColor.R(), g = activeColor.G(), b = activeColor.B(), a = activeColor.A();

            // Bottom (Y-)
            bufferBuilder.vertex(x0, y0, z0).color(r, g, b, a);
            bufferBuilder.vertex(x1, y0, z0).color(r, g, b, a);
            bufferBuilder.vertex(x1, y0, z1).color(r, g, b, a);
            bufferBuilder.vertex(x0, y0, z1).color(r, g, b, a);

            // Top (Y+)
            bufferBuilder.vertex(x0, y1, z0).color(r, g, b, a);
            bufferBuilder.vertex(x0, y1, z1).color(r, g, b, a);
            bufferBuilder.vertex(x1, y1, z1).color(r, g, b, a);
            bufferBuilder.vertex(x1, y1, z0).color(r, g, b, a);

            // North (Z-)
            bufferBuilder.vertex(x0, y0, z0).color(r, g, b, a);
            bufferBuilder.vertex(x0, y1, z0).color(r, g, b, a);
            bufferBuilder.vertex(x1, y1, z0).color(r, g, b, a);
            bufferBuilder.vertex(x1, y0, z0).color(r, g, b, a);

            // South (Z+)
            bufferBuilder.vertex(x0, y0, z1).color(r, g, b, a);
            bufferBuilder.vertex(x1, y0, z1).color(r, g, b, a);
            bufferBuilder.vertex(x1, y1, z1).color(r, g, b, a);
            bufferBuilder.vertex(x0, y1, z1).color(r, g, b, a);

            // West (X-)
            bufferBuilder.vertex(x0, y0, z0).color(r, g, b, a);
            bufferBuilder.vertex(x0, y0, z1).color(r, g, b, a);
            bufferBuilder.vertex(x0, y1, z1).color(r, g, b, a);
            bufferBuilder.vertex(x0, y1, z0).color(r, g, b, a);

            // East (X+)
            bufferBuilder.vertex(x1, y0, z0).color(r, g, b, a);
            bufferBuilder.vertex(x1, y1, z0).color(r, g, b, a);
            bufferBuilder.vertex(x1, y1, z1).color(r, g, b, a);
            bufferBuilder.vertex(x1, y0, z1).color(r, g, b, a);
        }

        BuiltBuffer builtBuffer = bufferBuilder.endNullable();
        if (builtBuffer == null) return;

        GpuBuffer vertexBuffer = null;
        try {
            int indexCount = builtBuffer.getDrawParameters().indexCount();
            vertexBuffer = RenderSystem.getDevice()
                    .createBuffer(() -> "glow vertex buffer", GpuBuffer.USAGE_VERTEX, builtBuffer.getBuffer());

            Vec3d camPos = mc.gameRenderer.getCamera().getCameraPos();
            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            GpuTextureView colorView = mc.getFramebuffer().getColorAttachmentView();
            GpuTextureView depthView = mc.getFramebuffer().getDepthAttachmentView();

            matrix4fStack.pushMatrix();
            matrix4fStack.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);

            GpuBufferSlice[] uniforms = RenderSystem.getDynamicUniforms().writeTransforms(
                    new DynamicUniforms.TransformsValue(
                            new Matrix4f(matrix4fStack),
                            new Vector4f(1f, 1f, 1f, 1f),
                            new Vector3f(),
                            new Matrix4f()
                    )
            );

            RenderSystem.setShaderFog(uniforms[0]);
            GpuBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS).getIndexBuffer(indexCount);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            //GL11.glDisable(GL11.GL_CULL_FACE);
            try (RenderPass renderPass = RenderSystem.getDevice()
                    .createCommandEncoder()
                    .createRenderPass(() -> "glow", colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {

                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.setIndexBuffer(indexBuffer, RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS).getIndexType());
                renderPass.setUniform("DynamicTransforms", uniforms[0]);
                renderPass.setPipeline(RenderPipelines.DEBUG_FILLED_BOX); // alpha blended, no texture
                renderPass.drawIndexed(0, 0, indexCount, 1);
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_CULL_FACE);
            matrix4fStack.popMatrix();
            builtBuffer.close();
        } catch (Exception e) {
            builtBuffer.close();
        } finally {
            if (vertexBuffer != null) vertexBuffer.close();
        }
    }

    private static void addLine(BufferBuilder buf,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float r, float g, float b, float a) {
        float dx = x2-x1, dy = y2-y1, dz = z2-z1;
        float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        buf.vertex(x1,y1,z1).color(r,g,b,a).normal(dx/len, dy/len, dz/len).lineWidth(1f);
        buf.vertex(x2,y2,z2).color(r,g,b,a).normal(dx/len, dy/len, dz/len).lineWidth(1f);
    }

    public static void addTracer(PosColor posColor) {
        tracerQueue.add(posColor);
    }

    public static void clearTracers() {
        tracerQueue.clear();
    }

    public record Rotation(float yaw, float pitch) {
        public Vec3d toLookVec() {
            float radPerDeg = MathHelper.RADIANS_PER_DEGREE;
            float pi = MathHelper.PI;

            float adjustedYaw = -MathHelper.wrapDegrees(yaw) * radPerDeg - pi;
            float cosYaw = MathHelper.cos(adjustedYaw);
            float sinYaw = MathHelper.sin(adjustedYaw);

            float adjustedPitch = -MathHelper.wrapDegrees(pitch) * radPerDeg;
            float nCosPitch = -MathHelper.cos(adjustedPitch);
            float sinPitch = MathHelper.sin(adjustedPitch);

            return new Vec3d(sinYaw * nCosPitch, sinPitch, cosYaw * nCosPitch);
        }
    }


    public static Vec3d getClientLookVec(float partialTicks) {
        float yaw = Fullbright.client.player.getYaw(partialTicks);
        float pitch = Fullbright.client.player.getPitch(partialTicks);
        return new Rotation(yaw, pitch).toLookVec();
    }

    private static Vec3d getTracerOrigin(float partialTicks) {
        Vec3d camPos = getCameraPos();
        Vec3d look = getClientLookVec(partialTicks).multiply(10);

        if (Fullbright.client.options.getPerspective() == Perspective.THIRD_PERSON_FRONT)
            look = look.negate();

        return camPos.add(look);
    }

    public static Vec3d getCameraPos() {
        Camera camera = Fullbright.client.gameRenderer.getCamera();
        if(camera == null)
            return Vec3d.ZERO;

        return camera.getCameraPos();
    }

    private static List<Vec3d> computeVeinCenters(List<PosColor> queue) {
        List<List<PosColor>> veins = new ArrayList<>();
        boolean[] visited = new boolean[queue.size()];

        for (int i = 0; i < queue.size(); i++) {
            if (visited[i]) continue;

            List<PosColor> vein = new ArrayList<>();
            Queue<Integer> flood = new LinkedList<>();
            flood.add(i);
            visited[i] = true;

            // BFS flood fill to group adjacent blocks
            while (!flood.isEmpty()) {
                int idx = flood.poll();
                vein.add(queue.get(idx));

                for (int j = 0; j < queue.size(); j++) {
                    if (visited[j]) continue;
                    BlockPos a = queue.get(idx).pos;
                    BlockPos b = queue.get(j).pos;
                    int manhattan = Math.abs(a.getX() - b.getX())
                            + Math.abs(a.getY() - b.getY())
                            + Math.abs(a.getZ() - b.getZ());
                    if (manhattan <= 1) {
                        visited[j] = true;
                        flood.add(j);
                    }
                }
            }
            veins.add(vein);
        }

        // Average each vein into a single center point
        List<Vec3d> centers = new ArrayList<>();
        for (List<PosColor> vein : veins) {
            double ax = 0, ay = 0, az = 0;
            for (PosColor pc : vein) {
                ax += pc.pos.getX() + 0.5;
                ay += pc.pos.getY() + 0.5;
                az += pc.pos.getZ() + 0.5;
            }
            centers.add(new Vec3d(ax / vein.size(), ay / vein.size(), az / vein.size()));
        }
        return centers;
    }

    public static void renderTracers() {
        if (tracerQueue.isEmpty()) return;
        if (!Config.showTracers.get()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        Vec3d origin = getTracerOrigin(mc.getRenderTickCounter().getTickProgress(true));
        Vec3d camOffset = getCameraPos().negate();

        List<Vec3d> veinCenters = computeVeinCenters(new ArrayList<>(tracerQueue));

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(
                RenderPipelines.LINES.getVertexFormatMode(),
                RenderPipelines.LINES.getVertexFormat()
        );

        float sx = (float)(origin.x + camOffset.x);
        float sy = (float)(origin.y + camOffset.y);
        float sz = (float)(origin.z + camOffset.z);

        PosColor rep = tracerQueue.get(0);
        //float r = rep.R(), g = rep.G(), b = rep.B(), a = rep.A();
        float r = activeColor.R(), g = activeColor.G(), b = activeColor.B(), a = activeColor.A();

        for (Vec3d center : veinCenters) {
            float ex = (float)(center.x + camOffset.x);
            float ey = (float)(center.y + camOffset.y);
            float ez = (float)(center.z + camOffset.z);
            addLine(bufferBuilder, sx, sy, sz, ex, ey, ez, r, g, b, a);
        }

        BuiltBuffer builtBuffer = bufferBuilder.endNullable();
        if (builtBuffer == null) return;

        try {
            int indexCount = builtBuffer.getDrawParameters().indexCount();
            GpuBuffer vertexBuffer = RenderSystem.getDevice()
                    .createBuffer(() -> "tracer vertex buffer", GpuBuffer.USAGE_VERTEX, builtBuffer.getBuffer());

            Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
            GpuTextureView colorView = mc.getFramebuffer().getColorAttachmentView();
            GpuTextureView depthView = mc.getFramebuffer().getDepthAttachmentView();

            matrix4fStack.pushMatrix();

            GpuBufferSlice[] uniforms = RenderSystem.getDynamicUniforms().writeTransforms(
                    new DynamicUniforms.TransformsValue(
                            new Matrix4f(matrix4fStack),
                            new Vector4f(1f, 1f, 1f, 1f),
                            new Vector3f(),
                            new Matrix4f()
                    )
            );

            RenderSystem.setShaderFog(uniforms[0]);
            GpuBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.LINES).getIndexBuffer(indexCount);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            try (RenderPass renderPass = RenderSystem.getDevice()
                    .createCommandEncoder()
                    .createRenderPass(() -> "tracers", colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {

                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.setIndexBuffer(indexBuffer, RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.LINES).getIndexType());
                renderPass.setUniform("DynamicTransforms", uniforms[0]);
                renderPass.setPipeline(RenderPipelines.LINES);
                renderPass.drawIndexed(0, 0, indexCount, 1);
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            matrix4fStack.popMatrix();
            vertexBuffer.close();
            builtBuffer.close();
        } catch (Exception e) {
            builtBuffer.close();
        }
    }
}