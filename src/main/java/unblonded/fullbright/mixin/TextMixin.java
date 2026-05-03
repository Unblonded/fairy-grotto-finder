package unblonded.fullbright.mixin;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Text.class)
public interface TextMixin {

    @Inject(method = "getString", at = @At("HEAD"), cancellable = true)
    private void stripTranslations(CallbackInfoReturnable<String> cir) {
        Text self = (Text)(Object)this;
        TextContent content = self.getContent();

        if (content instanceof TranslatableTextContent translatable) {
            String fallback = translatable.getFallback();
            if (fallback != null && !fallback.isEmpty()) {
                cir.setReturnValue(fallback);
                return;
            }

            cir.setReturnValue(translatable.getKey());
        }
    }
}