package com.epam.xmcy.util;

import com.epam.xmcy.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StartupUtils {

    @Autowired
    private CsvService csvService;

    @PostConstruct
    public void processDataOnStartup() {
        csvService.refreshData();
    }
}
