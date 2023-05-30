package me.nogari.nogari.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableEncryptableProperties
@Configuration
public class JasyptConfig {
	public static final String JASYPT_STRING_ENCRYPTOR = "jasyptStringEncryptor";

	// 복호화 키(jasypt.encryptor.password)는 Application 실행 시 외부 env 통해주입받음
	// Jar : Djasypt.encryptor.password=jasypt_password.!
	@Value("{jasypt.encryptor.password}") private String ENCRYPT_KEY;
	@Value("{jasypt.encryptor.salt-generator}") private String SALT_GENERATOR;

	@Bean(JASYPT_STRING_ENCRYPTOR)
	public StringEncryptor createEncryptor() {

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

		encryptor.setPassword(ENCRYPT_KEY);
		encryptor.setAlgorithm("PBEWITHMD5ANDTRIPLEDES");
		encryptor.setSaltGenerator(new StringFixedSaltGenerator(SALT_GENERATOR));		// AES 사용 시 설정필수 : salt 생성방식 '고정'으로 변경

		log.info("Jasypt Config Completed");

		return encryptor;
	}






}
