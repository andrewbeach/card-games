(ns war.game-logic)

(def suits {:clubs :diamonds :hearts :spades})
(def ranks
  {:two {:text "2" :value 2}
   :three {:text "3" :value 3}
   :four {:text "4" :value 4}
   :five {:text "5" :value 5}
   :six {:text "6" :value 6}
   :seven {:text "7" :value 7}
   :eight {:text "8" :value 8}
   :nine {:text "9" :value 9}
   :ten {:text "10" :value 10}
   :jack {:text "jack" :value 11}
   :queen {:text "queen" :value 12}
   :king {:text "king" :value 13}
   :ace {:text "ace" :value 14}})

(defn add-rank-to-cards [rank cards]
  (-> cards
    (conj {:suit :clubs :rank rank})
    (conj {:suit :diamonds :rank rank})
    (conj {:suit :hearts :rank rank})
    (conj {:suit :spades :rank rank})))

(def rank-vals (map (fn [[k v]] v) ranks))

(defn build-deck []
  (shuffle
    (reduce (fn [deck next] (add-rank-to-cards next deck)) [] rank-vals)))

(defn build-decks []
  (map #(into #queue [] %) (partition 26 (build-deck))))

(defn sum-value [deck]
  (reduce
    (fn [count next]
      (+ count (get-in next [:rank :value])))
    0
    deck))

(defn card-description [card]
  (str (clojure.string/capitalize (get-in card [:rank :text]))
    " of "
    (clojure.string/capitalize (name (get card :suit)))))

(defn average-value [deck]
  (/ (sum-value deck) (count deck)))

(defn value [card]
  (get-in card [:rank :value]))

(defn power-score [deck]
  (* 100 (/ (average-value deck) 8)))

(defn compare-cards [card-a card-b]
  (let [val-a (value card-a)
        val-b (value card-b)]
    (cond
      (= val-a val-b) :war
      (> val-a val-b) :win
      (< val-a val-b) :lose)))

(defn add-card-to-deck [deck card]
  (conj deck card))

(defn trigger-game-end [] (println "game over"))

(defn handle-endgame [threshold pot deck-a deck-b]
  (if (or (empty? deck-a) (> threshold (count deck-a)))
    (do (println "B Wins") (into [] [deck-a (into deck-b pot)]))
    (do (println "A Wins") (into [] [(into deck-a pot) deck-b]))))

(defn card-filename [card]
  (str "images/png/" (get-in card [:rank :text]) "_of_" (name (:suit card)) ".png"))

;; (Pot, Decks) -> Decks
(defn do-turn [threshold pot decks]
  (let [deck-a (first decks)
        deck-b (second decks)]
    (if (or (empty? deck-a) (empty? deck-b) (> threshold (count deck-a)) (> threshold (count deck-b)))
      (handle-endgame threshold pot deck-a deck-b)
      (let [deck-a (first decks)
            card-a (peek deck-a)
            deck-b (second decks)
            card-b (peek deck-b)
            result-for-a (compare-cards card-a card-b)
            pot (into pot [card-a card-b])
            new-deck-a (pop deck-a)
            new-deck-b (pop deck-b)]
        (case result-for-a
          :win (into [] [(into new-deck-a (shuffle pot)) new-deck-b])
          :lose (into [] [new-deck-a (into new-deck-b (shuffle pot))])
          :war (do-turn threshold (into pot [(peek new-deck-a) (peek new-deck-b)])
                 (into [] [(pop new-deck-a) (pop new-deck-b)])))))))
