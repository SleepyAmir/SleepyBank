package bank.app.model.service;

import bank.app.model.entity.Card;
import bank.app.model.repository.CardRepository;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class CardService {
    // Remove the instance variable cardRepository to avoid sharing across threads
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

    public Card findByCardNumber(String cardNumber) throws Exception { // Fixed typo from query
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