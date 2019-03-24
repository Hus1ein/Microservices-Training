package com.in28minutes.microservices.currencyconversionservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeServiceProxy proxy;

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrency(@PathVariable("from") String from, @PathVariable("to") String to,
                                                  @PathVariable("quantity") BigDecimal quantity) {
        // The old way to connect to another micro service.
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversionBean.class, uriVariables);

        CurrencyConversionBean currencyConversionBean = responseEntity.getBody();

        return new CurrencyConversionBean(currencyConversionBean.getId(), from, to,
                currencyConversionBean.getConversionMultiple(), quantity,
                quantity.multiply(currencyConversionBean.getConversionMultiple()), currencyConversionBean.getPort());

    }

    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrencyFeign(@PathVariable("from") String from, @PathVariable("to") String to,
                                                  @PathVariable("quantity") BigDecimal quantity) {
        // The new way to connect to another micro service.

        CurrencyConversionBean currencyConversionBean = proxy.retrieveExchangeValueNewName(from, to);

        return new CurrencyConversionBean(currencyConversionBean.getId(), from, to,
                currencyConversionBean.getConversionMultiple(), quantity,
                quantity.multiply(currencyConversionBean.getConversionMultiple()), currencyConversionBean.getPort());

    }

}
