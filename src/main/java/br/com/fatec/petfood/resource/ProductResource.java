package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.enums.Pets;
import br.com.fatec.petfood.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/product")
public class ProductResource {

    private final ProductService productService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestBody ProductDTO productDTO,
            @RequestParam(value = "pets") Pets pets
    ) {
        return productService.createProduct(productDTO, pets);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/title", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProductByName(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "sellerName") String sellerName
    ) {
        return productService.getProductByTitle(title, sellerName);
    }
}
