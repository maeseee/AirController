import { TestBed } from '@angular/core/testing';

import { CardViewService } from './CardViewService';

describe('CurrentClimateDataPoint', () => {
  let service: CardViewService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CardViewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
