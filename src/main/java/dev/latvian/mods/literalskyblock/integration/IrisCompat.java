package dev.latvian.mods.literalskyblock.integration;

import com.mojang.logging.LogUtils;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.client.renderer.LevelRenderer;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public class IrisCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Field PIPELINE;

    static {
        Field pipeline;
        try {
            //noinspection JavaReflectionMemberAccess
            pipeline = LevelRenderer.class.getDeclaredField("pipeline");
            pipeline.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            pipeline = null;
            LOGGER.error("Failed to get Iris pipeline field", e);
        }
        PIPELINE = pipeline;
    }

    public static void preRender(LevelRenderer renderer) {
        if (PIPELINE == null) return;
        try {
            final WorldRenderingPipeline pipeline = Iris.getPipelineManager().preparePipeline(Iris.getCurrentDimension());
            PIPELINE.set(renderer, pipeline);
            if (pipeline != null) {
                pipeline.setOverridePhase(WorldRenderingPhase.NONE);
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Exception in preRender", e);
        }
    }

    public static void postRender(LevelRenderer renderer) {
        if (PIPELINE == null) return;
        try {
            PIPELINE.set(renderer, null);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Exception in postRender", e);
        }
    }

    public static boolean shadersEnabled() {
        return IrisApi.getInstance().isShaderPackInUse();
    }
}
