package br.com.gastosmensais;

import org.springframework.boot.SpringApplication;

public class TestGastosMensaisApplication {

    public static void main(String[] args) {
        SpringApplication.from(GastosMensaisApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
