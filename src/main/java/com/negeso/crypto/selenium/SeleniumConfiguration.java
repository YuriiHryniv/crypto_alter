package com.negeso.crypto.selenium;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Log4j2
@Configuration
public class SeleniumConfiguration {

    /* Note: should use Chrome driver version correlative with the Chrome version installed. At the moment program uses next setups:
    - locally: Chrome browser (version 105.0.5195.102), Chrome driver (version 105.0.5195.52);
    - sever: Chrome browser (version ?, localization: "/usr/bin/google-chrome"), Chrome driver (version 105.0.5195.52).*/
    @PostConstruct
    void postConstruct() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.setProperty("webdriver.chrome.driver",
                    "src/main/resources/META-INF/resources/drivers/chromedriver_win_v_109.0.5414.74.exe");
        } else {
            System.setProperty("webdriver.chrome.driver",
                    "/var/www/share-new/sites-demo/crypto.negeso.com/www/WEB-INF/classes/META-INF/resources/drivers/chromedriver_lin_v_109.0.5414.74.exe");
        }
    }

    @Bean
    public ChromeDriver driver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            chromeOptions.setBinary("/usr/bin/google-chrome");
        }
        log.info("Chromedriver for ".concat(System.getProperty("os.name").concat(" was configured.")));
        return new ChromeDriver(chromeOptions);
    }

}
