(ns war.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [war.game-logic :as game]))

(defn title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label (str "Playing War!")
       :level :level1])))

(defn counter [label & vals]
  [:div.counter
   [:p label]
   (for [v vals]
     [:p v])])

(defn counters []
  (let [decks (re-frame/subscribe [:decks])]
    (fn []
      [re-com/h-box
       :children [[counter
                   "Player 1"
                   (count (first @decks))
                   (game/sum-value (first @decks))
                   (str (.toFixed (* 100 (/ (game/sum-value (first @decks)) 416)) 0) "%")]
                  [counter
                   "Player 2"
                   (count (second @decks))
                   (game/sum-value (second @decks))
                   (str (.toFixed (* 100 (/ (game/sum-value (second @decks)) 416)) 0) "%")]]]
      )))

(defn deck-view []
  (let [decks (re-frame/subscribe [:decks])]
    [:img.deck {:src "images/png/back.png"
                :on-click #(re-frame/dispatch [:update-decks (game/do-turn 15 [] @decks)])}]))

(defn game-board []
  (let [decks (re-frame/subscribe [:decks])]
    (fn []
      [re-com/h-box
       :width "100%"
       :children [[deck-view]
                  [:img.single-card {:src (game/card-filename (peek (first @decks)))}]
                  [:img.single-card {:src (game/card-filename (peek (second @decks)))}]
                  [deck-view]]]
      )))

(defn refresh-game []
  (fn []
    [re-com/button
     :label "New Game"
     :on-click #(re-frame/dispatch [:update-decks (game/build-decks)])]))

(defn main-panel []
  (fn []
    [re-com/v-box
     :height "100%"
     :children [[title]
                [game-board]
                [counters]
                [refresh-game]]]))

;; (loop [decks samples
;;        n 66
;;        turns 0]
;;   (if (= 0 n)
;;     decks
;;     (recur (do-turn 12 [] decks) (dec n) (inc turns))))
