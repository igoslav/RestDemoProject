package com.epam.xmcy.rest;

import com.epam.xmcy.exception.BusinessServiceException;
import com.epam.xmcy.model.Cryptocurrency;
import com.epam.xmcy.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

/**
 * Main controller to interact with cryptocurrencies data.
 */
@Controller
@RequestMapping("/cryptocurrencies")
public class Resource {

    @Autowired
    private CsvService csvService;

    /**
     * Method returns a descending sorted list of all the cryptos,
     * based on the normalized range.
     *
     * @return list of cryptocurrencies.
     */
    @GetMapping
    @ResponseBody
    public List<Cryptocurrency> getAllCryptocurrencies() {
        return csvService.getAllCryptocurrencies();
    }

    /**
     * Method returns statistics for a particular cryptocurrency within range.
     *
     * @param crypto   cryptocurrency short name (BTC, ETH, etc.).
     * @param fromDate start date for crypto stats, optional, format dd-MM-yyyy.
     * @param toDate   end date for crypto stats, optional, format dd-MM-yyyy.
     * @return cryptocurrency statistic object.
     */
    @GetMapping("/{crypto}")
    @ResponseBody
    public Cryptocurrency getCryptoStatistics(@PathVariable String crypto,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate) {
        if (crypto == null || "".equals(crypto)) {
            throw new IllegalArgumentException("Please provide valid cryptocurrency short name.");
        }

        if (fromDate == null) {
            fromDate = LocalDate.MIN;
        }

        if (toDate == null) {
            toDate = LocalDate.now();
        }

        Cryptocurrency cryptocurrency = csvService.getCryptocurrencyByName(crypto);
        if (cryptocurrency == null) {
            throw new BusinessServiceException("Cryptocurrency not found or not supported yet.");
        }

        return cryptocurrency;
    }

    /**
     * Method returns cryptocurrency with the highest normalized range for a specific date.
     *
     * @param fromDate start date to check, format dd-MM-yyyy.
     * @param toDate end date to check, format dd-MM-yyyy.
     * @return cryptocurrency object.
     */
    @GetMapping("/recommendation")
    @ResponseBody
    public List<Cryptocurrency> getRecommendationByDate(@RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate) {
        if (fromDate == null) {
            fromDate = LocalDate.now();
        }

        if (toDate == null) {
            toDate = fromDate;
        }

        // Actually, we need to return only 1 crypto,
        // but this endpoint returns list of all cryptos sorted by normalized range
        return csvService.getRecommendationByDate(fromDate, toDate);
    }

    @GetMapping("/refresh")
    @ResponseBody
    public void refreshStats() {
        csvService.refreshData();
    }
}
