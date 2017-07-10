(ns war.db
  (:require [war.game-logic :as game]))

(def default-db
  {:name "re-frame"
   :decks (game/build-decks)})
