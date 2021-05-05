package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/search")
public class SearchResource {

    private final SearchService searchService;

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchSeller(
            @RequestParam(value = "productTitle") String productTitle,
            @RequestParam(value = "isWeek") Boolean isWeek,
            @RequestParam(value = "localTime") String localTime
    ) {
        return searchService.searchSeller(productTitle, isWeek, localTime);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/seller/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchSellerProducts(
            @RequestParam(value = "sellerName") String sellerName,
            @RequestParam(value = "productTitle", required = false) String productTitle
    ) {
        return searchService.searchSellerProducts(sellerName, productTitle);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/seller/category", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchSellerByCategory(
            @RequestParam(value = "category") Category category,
            @RequestParam(value = "isWeek") Boolean isWeek,
            @RequestParam(value = "localTime") String localTime
    ) {
        return searchService.searchSellerByCategory(category, isWeek, localTime);
    }
}
