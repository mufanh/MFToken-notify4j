package com.mufanh.mftoken.notify.config;

import com.mufanh.mftoken.contract.MFToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;

@Configuration
@Slf4j
public class ContractConfiguration {

    @Scope("prototype")
    @Bean
    public Web3j web3j(ContractConfig contractConfig) {
        return Web3j.build(new HttpService(contractConfig.getUrl()));
    }

    @Bean
    public MFToken mftoken(Web3j web3j, ContractConfig contractConfig) throws IOException {
        MFToken contract;
        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            log.info("客户端版本:{}", clientVersion);

            // 以某个用户的身份调用合约
            TransactionManager transactionManager = new ClientTransactionManager(web3j, contractConfig.getSenderAddress());
            //加载智能合约
            contract = MFToken.load(contractConfig.getAddress(), web3j, transactionManager, new DefaultGasProvider());
            return contract;
        } catch (IOException e) {
            log.info("加载合约异常", e);
            throw e;
        }
    }

    //监听这里才用每次都生成一个新的对象，因为同时监听多个事件不能使用同一个实例
    @Scope("prototype")
    @Bean
    public EthFilter transferFilter(MFToken mftoken, Web3j web3j) throws IOException {
        // 获取启动时监听的区块
        Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
        return new EthFilter(DefaultBlockParameter.valueOf(request.send().getBlockNumber()),
                DefaultBlockParameterName.LATEST, mftoken.getContractAddress());
    }
}
