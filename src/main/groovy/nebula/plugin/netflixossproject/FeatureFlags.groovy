package nebula.plugin.netflixossproject
import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class FeatureFlags {
    public static final String GRADLE_METADATA_SHADOW_PUBLISHING_SUPPORT = "nebula.features.gradleMetadata.shadowPublishing.enabled"

    private FeatureFlags() {}

    static boolean isFeatureEnabled(Project project, String featureFlag, boolean defaultValue) {
        return project.findProperty(featureFlag)?.toString() != null ? Boolean.valueOf(project.findProperty(featureFlag)?.toString()) : defaultValue
    }
}
