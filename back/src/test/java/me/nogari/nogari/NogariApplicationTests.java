package me.nogari.nogari;

import static org.hibernate.id.enhanced.StandardOptimizerDescriptor.*;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.registry.AlgorithmRegistry;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;
import me.nogari.nogari.config.JasyptConfig;

@SpringBootTest
@Slf4j
class NogariApplicationTests {


	@Autowired
	private JasyptConfig jasyptConfig;

	@Test
	void contextLoads() {

	}

}
