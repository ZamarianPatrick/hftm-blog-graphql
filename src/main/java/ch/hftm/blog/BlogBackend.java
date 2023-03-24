package ch.hftm.blog;

import ch.hftm.blog.config.SecretConfig;
import ch.hftm.blog.utils.Encryptor;
import ch.hftm.blog.utils.KeyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.File;

@ApplicationScoped
public class BlogBackend {

    private static final Logger LOGGER = Logger.getLogger(BlogBackend.class);

    private static final String CONFIG_DIR = "data/config";

    @Inject
    KeyGenerator keyGenerator;

    private static SecretConfig secretConfig;
    private static Encryptor encryptor;

    void onStart(@Observes StartupEvent e) {

        File file = new File(CONFIG_DIR);
        if(!file.isDirectory()) {
            LOGGER.info("dir created: " + file.getAbsolutePath() + " " + file.mkdirs());
        }

        keyGenerator.generateKeys();

        loadSecretConfig();
        encryptor = new Encryptor(secretConfig);
    }

    public static void loadSecretConfig() {
        File file = new File(CONFIG_DIR + "/secret.json");
        ObjectMapper mapper = new ObjectMapper();
        if(file.exists()) {
            try {
                secretConfig = mapper.readValue(file, SecretConfig.class);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        } else {
            secretConfig = SecretConfig.builder()
                    .salt(Encryptor.generateRandomString(8))
                    .encryptionKey(Encryptor.generateAESKey())
                    .build();

            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, secretConfig);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    public static Encryptor getEncryptor () {
        return encryptor;
    }
}
