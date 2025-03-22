package bank.app.model.service;

import bank.app.model.entity.Card;
import bank.app.model.repository.CardRepository;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class CardService {
    private static final Logger log = Logger.getLogger(CardService.class);

    public void save(Card card) throws Exception {
        try (CardRepository repo = new CardRepository()) {
            repo.save(card);
        }
    }

    public void delete(Card card) throws Exception {
        try (CardRepository repo = new CardRepository()) {
            repo.remove(card.getId());
        }
    }

    public List<Card> findAll() throws Exception {
        try (CardRepository repo = new CardRepository()) {
            return repo.findAll();
        }
    }

    public List<Card> findByUserId(int userId) throws Exception {
        try (CardRepository repo = new CardRepository()) {
            return repo.findAll().stream()
                    .filter(card -> card.getUser().getId() == userId)
                    .collect(Collectors.toList());
        }
    }

    public Card findByCardNumber(String cardNumber) throws Exception {
        log.info("Requesting connection for findByCardNumber: " + cardNumber);
        try (CardRepository repo = new CardRepository()) {
            log.info("Connection acquired for findByCardNumber: " + cardNumber);
            return repo.findByCardNumber(cardNumber);
        } catch (Exception e) {
            log.error("Error in findByCardNumber: " + cardNumber, e);
            throw e;
        }
    }
}