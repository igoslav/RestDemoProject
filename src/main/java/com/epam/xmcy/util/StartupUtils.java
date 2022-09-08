package com.epam.xmcy.util;

import com.epam.xmcy.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Class to run specific jobs at the start of the application.
 */
@Component
public class StartupUtils {

    @Autowired
    private CsvService csvService;

    /**
     * Calculate crypto data at startup.
     */
    @PostConstruct
    public void processDataOnStartup() {
        csvService.refreshData();
    }
}
