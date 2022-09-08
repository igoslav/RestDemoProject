package com.epam.xmcy.rest;

import com.epam.xmcy.model.Cryptocurrency;
import com.epam.xmcy.service.CsvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @Operation(summary = "List all cryptocurrencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all cryptocurrencies sorted by normalized range") })
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
    @Operation(summary = "Get cryptocurrency by short name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return cryptocurrency info object") })

    @GetMapping("/{crypto}")
    @ResponseBody
    public Cryptocurrency getCryptoStatistics(@Parameter(description = "For example BTC, ETH, DOGE, etc.")
                                              @PathVariable String crypto,
                                              @Parameter(description = "Optional, not supported yet")
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                              @Parameter(description = "Optional, not supported yet")
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cryptocurrency not found or not supported yet.");
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
    @Operation(summary = "Recommend cryptocurrency based on normalized range within range." +
            "Right now support only for single day.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of all recommended cryptocurrencies sorted by normalized range") })
    @GetMapping("/recommendation")
    @ResponseBody
    public List<Cryptocurrency> getRecommendationByDate(@Parameter(description = "Recommendation from date")
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDate,
                                                        @Parameter(description = "Recommendation to date")
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
        List<Cryptocurrency> recommendation = csvService.getRecommendationByDate(fromDate, toDate);

        if (CollectionUtils.isEmpty(recommendation)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Recommendation not found for range: %s - %s", fromDate, toDate));
        }
        return recommendation;
    }

    @Operation(summary = "Recalculates all crypto stats based on resource.")
    @GetMapping("/refresh")
    @ResponseBody
    public void refreshStats() {
        csvService.refreshData();
    }
}
