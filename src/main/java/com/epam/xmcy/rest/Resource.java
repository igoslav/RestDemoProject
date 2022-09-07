package com.epam.xmcy.rest;

import com.epam.xmcy.model.Cryptocurrency;
import com.epam.xmcy.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
     * @param crypto cryptocurrency short name (BTC, ETH, etc.).
     * @param fromDate start date for crypto stats, format dd-MM-yyyy.
     * @param toDate end date for crypto stats, format dd-MM-yyyy.
     *
     * @return cryptocurrency statistic object.
     */
    @GetMapping("/{crypto}")
    @ResponseBody
    public Cryptocurrency getCryptoStatistics(@PathVariable String crypto,
                                      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate) {
        if (crypto == null || "".equals(crypto)) {
            throw new IllegalArgumentException("Please provide valid cryptocurrency short name.");
        }

        if (fromDate == null) {
            fromDate = LocalDate.MIN;
        }

        if (toDate == null) {
            toDate = LocalDate.now();
        }

        return csvService.getCryptocurrencyByName(crypto);
    }

    /**
     * Method returns cryptocurrency with the highest normalized range for a specific date.
     *
     * @param date date to check, format dd-MM-yyyy.
     *
     * @return cryptocurrency object.
     */
    @GetMapping("/recommendation")
    @ResponseBody
    public List<Cryptocurrency> getRecommendationByDate(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Please provide date");
        }

        return csvService.getRecommendationByDate(date);
    }
}
