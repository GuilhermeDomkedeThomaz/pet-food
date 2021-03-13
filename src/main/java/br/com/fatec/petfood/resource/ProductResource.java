package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestBody ProductDTO productDTO,
            @RequestParam(value = "category") Category category
    ) {
        return productService.createProduct(productDTO, category);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/title/seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findProductByTitleAndSellerName(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "sellerName") String sellerName
    ) {
        return productService.getProductByTitleAndSellerName(title, sellerName);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProduct(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "sellerName") String sellerName,
            @RequestBody ProductUpdateDTO productUpdateDTO,
            @RequestParam(value = "category") Category category
    ) {
        return productService.updateProduct(title, sellerName, productUpdateDTO, category);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteProduct(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "sellerName") String sellerName
    ) {
        return productService.deleteProduct(title, sellerName);
    }
}
