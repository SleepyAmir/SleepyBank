package bank.app.model.service;

import bank.app.model.entity.Card;
import bank.app.model.repository.CardRepository;
import java.util.List;
import java.util.stream.Collectors;

public class CardService {
    private CardRepository cardRepository = new CardRepository();

    public void save(Card card) throws Exception {
        try (CardRepository repo = new CardRepository()) {
            repo.save(card);
        }
    }

    public List<Card> findAll() throws Exception {
        try (CardRepository repo = new CardRepository()) {
            return repo.findAll();
        }
    }

    public List<Card> findByUserId(int userId) throws Exception {
        return findAll().stream()
                .filter(card -> card.getUser().getId() == userId)
                .collect(Collectors.toList());
    }

    public Card findByCardNumber(String cardNumber) throws Exception {
        return findAll().stream()
                .filter(card -> card.getCardNumber().equals(cardNumber))
                .findFirst()
                .orElse(null);
    }
}