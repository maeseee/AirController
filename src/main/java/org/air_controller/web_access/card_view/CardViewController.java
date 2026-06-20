package org.air_controller.web_access.card_view;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cardViews")
@RequiredArgsConstructor
public class CardViewController {

    private final CardViewService service;
    private final IndoorCardViewService indoorService;
    private final IndoorCardViewService outdoorService;

    @GetMapping("/{type}")
    public ResponseEntity<CardView> getCardGroup(@PathVariable String type) {
        final CardView data = switch (type.toLowerCase()) {
            case "system" -> service.getSystemCardView();
            case "indoor" -> indoorService.getCardView();
            case "outdoor" -> outdoorService.getCardView();
            case "confidence" -> service.getConfidenceCardView();
            case "statistics" -> service.getStatisticsCardView();
            default -> null;
        };
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        if (data.cards().isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(data);
    }
}
