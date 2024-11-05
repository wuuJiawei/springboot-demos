package xyz.easyboot.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.easyboot.handler.AuctionService;

@RestController
@RequestMapping("auction")
@AllArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping("all")
    public Object getAllAuctions() {
        return auctionService.getAllAuctions();
    }

}
