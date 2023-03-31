package com.zerobase.cms.order.application;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.order.service.ProductService;
import com.zerobase.cms.user.config.client.mailgun.SendMailForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class OrderApplicationTest {
    @Autowired
    private OrderApplication orderApplication;
    @Autowired
    private CartApplication cartApplication;
    @Autowired
    private UserClient userClient;
    @Autowired
    private ProductItemService productItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private String token = "token";

    @Test
    @Transactional
    void mailTextbuildertest(){
        Long customerId = 100L;

        cartApplication.clearCart(customerId);

        Product p = add_product();
        System.out.println(p.toString());
        Product result = productRepository.findWithProductItemsById(p.getId()).get();
        System.out.println(result);
        Cart cart = cartApplication.addCart(customerId,makeAddForm(result));

        StringBuilder mailText=new StringBuilder("");

        for(Cart.Product product : cart.getProducts()){
            for(Cart.ProductItem cartItem : product.getItems()){
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount()-cartItem.getCount());

                //메일 내용 추가
                mailText.append("\n상품 : " +productItem.getProduct()+ " 옵션 : " + productItem.getName() +" 가격 : " +productItem.getPrice());
            }
        }

        //주문내역 메일보내기

        mailText.append("\n 총가격 : "+ "총가격");

        SendMailForm sendMailForm = SendMailForm.builder()
                .from("zeroOrder@no-reply.com")
                .to("고객이메일")
                .subject("주문이 완료되었습니다.")
                .text(mailText.toString())
                .build();

        System.out.println(mailText);
    }



    AddProductCartForm makeAddForm(Product p){
        AddProductCartForm.ProductItem productItem =
                AddProductCartForm.ProductItem.builder()
                        .id(p.getProductItems().get(0).getId())
                        .name(p.getProductItems().get(0).getName())
                        .count(5)
                        .price(20000)
                        .build();
        return AddProductCartForm.builder()
                .id(p.getId())
                .sellerId(p.getSellerId())
                .name(p.getName())
                .description(p.getDescription())
                .items(List.of(productItem)).build();
    }


    private Long customerId=1L;




    Product add_product(){
        Long sellerId = 1L;
        AddProductForm form = makeProductForm("나이키 에어포스","신발",3);
        return productService.addProduct(sellerId,form);
    }

    private static AddProductForm makeProductForm(String name,String description,int itemCount){
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for(int i=0;i<itemCount;i++){
            itemForms.add(makeProductItemForm(null,name+i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId,String name){
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(10)
                .build();
    }

}