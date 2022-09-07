package com.epam.xmcy.rest;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Main controller to interact with cryptocurrencies data.
 */
@Controller
@RequestMapping("/cryptocurrencies")
public class Resource {

    /**
     * Method returns a descending sorted list of all the cryptos,
     * based on the normalized range.
     *
     * @return list of cryptocurrencies.
     */
    @GetMapping
    @ResponseBody
    public String getAllCryptos() {
        return "Hello world!";
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
    public String getCryptoStatistics(@PathVariable String crypto,
                                      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDate) {
        return "Hello world " + crypto;
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
    public String getRecommendationByDate(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
        return "Hello world " + date;
    }
}
