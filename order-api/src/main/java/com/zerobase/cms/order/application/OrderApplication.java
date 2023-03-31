package com.zerobase.cms.order.application;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.user.config.client.MailgunClient;
import com.zerobase.cms.user.config.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderApplication {

    private final CartApplication cartApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;
    private final MailgunClient mailgunClient;

    @Transactional
    public void order(String token, Cart cart) {

        StringBuilder mailText=new StringBuilder("");
        // 1번 : 주문 시 기존 카트 버림.
        // 2번 : 선택주문 : 내가 사지 않은 아이템을 살려야 함
        // -- 여러분이 숙제
        Cart orderCart = cartApplication.refreshCart(cart);
        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ErrorCode.ORDER_FAIL_CHECK_CART);
        }
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(cart);
        if(customerDto.getBalance() < totalPrice){
            throw new CustomException(ErrorCode.ORDER_FAIL_NO_MONEY);
        }
        // 롤백 계획에 대해서 생각해야 함.
        userClient.changeBalance(token,
                ChangeBalanceForm.builder()
                        .from("USER")
                        .message("Order")
                        .money(-totalPrice)
                        .build());

        for(Cart.Product product : orderCart.getProducts()){
            for(Cart.ProductItem cartItem : product.getItems()){
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount()-cartItem.getCount());

                //메일 내용 추가
                mailText.append("\n상품 : " +productItem.getProduct().getName()+ " 옵션 : " + productItem.getName() +" 가격 : " +productItem.getPrice());
            }
        }

        //주문내역 메일보내기

        mailText.append("\n 총가격 : "+ totalPrice);
        log.info("mailText",mailText);

        SendMailForm sendMailForm = SendMailForm.builder()
                .from("zeroOrder@no-reply.com")
                .to(customerDto.getEmail())
                .subject("주문이 완료되었습니다.")
                .text(mailText.toString())
                .build();

        mailgunClient.sendEMail(sendMailForm);
    }



    private Integer getTotalPrice(Cart cart) {

        return cart.getProducts().stream().flatMapToInt(
                        product -> product.getItems().stream().flatMapToInt(
                                productItem -> IntStream.of(productItem.getPrice() * productItem.getCount())))
                .sum();

    }
    //결제를 위해 필요한것
    //1 . 물건이 주문 가능상태인지 확인하기
    //2 . 가격 변동이 있었는지
    //3 . 예치금이 충분한지
    //4 . 결제 이후 상품 재고 관리

}
