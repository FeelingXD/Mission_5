package com.zerobase.cms.user.service.seller;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    public Optional<Seller> findByIdAndEmail(Long id, String email){
        return sellerRepository.findByIdAndEmail(id,email);
    }
    public Optional<Seller> findValidSeller(String email,String password){
        return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email,password);
    }

    public Seller singUp(SignUpForm form){
        return sellerRepository.save(Seller.from(form));
    }

    public boolean isEmailExist(String email){
        return sellerRepository.findByEmail(email).isPresent();
    }
    @Transactional
    public void verifyEmail(String email,String code){
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        if(seller.isVerify()){
            throw new CustomException(ErrorCode.ALREADY_VERIFY);
        }
        else if(!seller.getVerificationCode().equals(code)){
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }
        else if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            throw  new CustomException(ErrorCode.EXPIRE_CODE);
        }
        seller.setVerify(true);
    }

    @Transactional
    public LocalDateTime changeSellerValidationEmail(Long sellerId,String verificationCode){
        Optional<Seller> customerOptional = sellerRepository.findById(sellerId);

        if(customerOptional.isPresent()){
            Seller seller=customerOptional.get();

            seller.setVerificationCode(verificationCode);
            seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return seller.getVerifyExpiredAt();
        }
        throw new CustomException(ErrorCode.NOT_FOUND_USER);

    }


    public Optional<Seller> findValidCustomerByEmailAndPassword(String email, String password) {
        return sellerRepository.findByEmail(email).stream()
                .filter(customer -> customer.getPassword().equals(password)&&customer.isVerify()
                ).findFirst();
    }
}