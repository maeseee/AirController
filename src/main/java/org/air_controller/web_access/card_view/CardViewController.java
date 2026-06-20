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

    private final SystemCardViewService systemService;
    private final ConfidenceCardViewService confidenceCardViewService;
    private final StatisticsCardViewService statisticsCardViewService;
    private final IndoorCardViewService indoorService;
    private final IndoorCardViewService outdoorService;
    private final SolarEventsService solarEventsService;

    @GetMapping("/{type}")
    public ResponseEntity<CardView> getCardGroup(@PathVariable String type) {
        final CardView data = switch (type.toLowerCase()) {
            case "system" -> systemService.getCardView();
            case "confidence" -> confidenceCardViewService.getCardView();
            case "statistics" -> statisticsCardViewService.getCardView();
            case "indoor" -> indoorService.getCardView();
            case "outdoor" -> outdoorService.getCardView();
            case "solar_events" -> solarEventsService.getCardView();
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
