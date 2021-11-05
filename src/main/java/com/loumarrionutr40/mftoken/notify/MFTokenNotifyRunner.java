package com.loumarrionutr40.mftoken.notify;

import com.loumarrionutr40.mftoken.contract.MFToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;

import java.math.BigInteger;
import static com.loumarrionutr40.mftoken.contract.MFToken.TRANSFER_EVENT;
import static org.web3j.tx.Contract.staticExtractEventParameters;

@Component
@Slf4j
public class MFTokenNotifyRunner implements ApplicationRunner {

    private final Web3j web3j;

    // 如果多个监听，必须要注入新的过滤器
    private final EthFilter transferFilter;

    @Autowired
    public MFTokenNotifyRunner(Web3j web3j, EthFilter transferFilter) {
        this.web3j = web3j;
        this.transferFilter = transferFilter;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        processTransferEvent();
    }

    /**
     * 收到上链事件
     */
    private void processTransferEvent() throws InterruptedException {
        transferFilter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));

        log.info("启动监听Transfer事件");
        web3j.ethLogFlowable(transferFilter)
                .subscribe(e -> {
                    EventValues eventValues = staticExtractEventParameters(TRANSFER_EVENT, e);
                    MFToken.TransferEventResponse data = new MFToken.TransferEventResponse();
                    data.log = e;
                    data._from = (String) eventValues.getIndexedValues().get(0).getValue();
                    data._to = (String) eventValues.getIndexedValues().get(1).getValue();
                    data._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                    processTransferEvent(data);
                });
    }

    private void processTransferEvent(MFToken.TransferEventResponse data) {
        log.info("Transfer事件:from={},to={},value={}", data._from, data._to, data._value);
    }
}
