(ns war.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [war.game-logic :as game]
            [reagent.core :as reagent]))

(defn title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label (str "Playing Cards...")
       :level :level1])))

(defn counter [label & vals]
  [:div.counter
   [:p.counter-label label]
   (for [v vals]
     [:p.counter-value v])])

(defn counters []
  (let [decks (re-frame/subscribe [:decks])]
    (fn []
      [re-com/h-box
       :children [[counter
                   "Player 1"
                   (str (count (first @decks)) " cards")
                   (str (game/sum-value (first @decks)) " points")
                   (str (.toFixed (* 100 (/ (game/sum-value (first @decks)) 416)) 0) "% of points")]
                  [counter
                   "Player 2"
                   (str (count (second @decks)) " cards")
                   (str (game/sum-value (second @decks)) " points")
                   (str (.toFixed (* 100 (/ (game/sum-value (second @decks)) 416)) 0) "% of points")]]]
      )))

(defn deck-view []
  (let [decks (re-frame/subscribe [:decks])]
    [:img.deck {:src "images/png/back.png"
                :on-click #(re-frame/dispatch [:update-decks (game/do-turn 15 [] @decks)])}]))

(def pot-state (reagent/atom {:drop-area {:class "ui-widget-header"
                                          :text "Drop here"}}))

(defn card-did-mount [this]
  (.draggable (js/$ (reagent/dom-node this))))

(defn card-render [card]
    [:div.ui-widget-content {:style {:width "138px"
                                     :height "200px"
                                     :border-radius "6px"}}
     [:img.single-card {:style {:width "138px"
                                :height "200px" }
                        :src (game/card-filename card)}]])

(defn card-view [card]
  (reagent/create-class {:reagent-render card-render
                         :component-did-mount card-did-mount}))

(defn pot-did-mount [this]
  (.droppable (js/$ (reagent/dom-node this))
    #js {:drop (fn []
                 (swap! pot-state assoc-in [:drop-area :class] "ui-widget-header ui-state-highlight")
                 (swap! pot-state assoc-in [:drop-area :text] "Dropped!"))}))

(defn pot-render []
  (let [class (get-in @pot-state [:drop-area :class])
        text (get-in @pot-state [:drop-area :text])]
    [:div {:class class
           :style {:width "500px"
                   :height "300px"
                   :padding "0.5em"
                   :margin "10px"}}
     [:p text]]))

(defn pot-area []
  (reagent/create-class {:reagent-render pot-render
                         :component-did-mount pot-did-mount}))

(defn game-board []
  (let [decks (re-frame/subscribe [:decks])]
    (fn []
      [re-com/h-box
       :width "100%"
       :children [[deck-view]
                  [card-view (peek (first @decks))]
                  [card-view (peek (second @decks))]
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
                [pot-area]
                [counters]
                [refresh-game]]]))

;; (loop [decks samples
;;        n 66
;;        turns 0]
;;   (if (= 0 n)
;;     decks
;;     (recur (do-turn 12 [] decks) (dec n) (inc turns))))

