import {CardView} from './CardView';

export interface CardGroup {
  from: string; // ISO-8601 Datumstring
  cards: CardView[];
}
