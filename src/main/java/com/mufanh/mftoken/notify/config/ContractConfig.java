package com.mufanh.mftoken.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 合约配置
 */
@ConfigurationProperties(prefix = "contract")
@Component
@Data
public class ContractConfig {

    private String address;

    private String url;

    private String senderAddress;
}
