package it.fanciullini.data.service;

import it.fanciullini.data.entity.PaymentsLog;
import it.fanciullini.data.entity.User;
import it.fanciullini.data.repo.PaymentsLogRepository;
import it.fanciullini.response.PaymentsLogResponse;
import it.fanciullini.utility.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentsLogService
{

    @Value("${payment.warning.threshold}")
    private Integer paymentWarningThreshold;

    @Autowired
    private PaymentsLogRepository paymentsLogRepository;

    public PaymentsLog findById(Long id) {
        return paymentsLogRepository.findById(id);
    }

    private Page<PaymentsLog> getList(Pageable pageRequest){
        Page<PaymentsLog> paymentsLogs = paymentsLogRepository.getList(pageRequest);
        if (paymentsLogs.getContent().size()==0){
            throw new ArrayIndexOutOfBoundsException();
            //pageRequest = new PageRequest(pageRequest.getPageNumber()-1, pageRequest.getPageSize());
            //paymentsLogs = paymentsLogRepository.getList(pageRequest);
        }
        return paymentsLogs;
    }

    public List<PaymentsLogResponse> getFilteredList(){
        int page = 0;
        int limit = 10;
        return getFilteredList(page, limit);
    }

    public List<PaymentsLogResponse> getFilteredList(int page) {
        int limit = 10;
        return getFilteredList(page, limit);
    }

    public List<PaymentsLogResponse> getFilteredList(int page, int limit){
        Pageable pageRequest = new PageRequest(page, limit);
        Page<PaymentsLog> paymentsLogList = this.getList(pageRequest);
        List<PaymentsLogResponse> paymentsLogResponseList = new ArrayList<>();
        for(PaymentsLog paymentsLog : paymentsLogList){
            PaymentsLogResponse tmp = new PaymentsLogResponse(paymentsLog, paymentWarningThreshold);
            paymentsLogResponseList.add(tmp);
        }
        return paymentsLogResponseList;
    }

    public Double importTotalByUser(User user) {
        Double total = paymentsLogRepository.importTotalByUser(user.getUsername(), StatusEnum.PAYED);
        return total != null ? total : new Double(0);
    }

    public PaymentsLog save(PaymentsLog paymentsLog){
        return paymentsLogRepository.save(paymentsLog);
    }
}
