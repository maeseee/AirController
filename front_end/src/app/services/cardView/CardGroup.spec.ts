import { TestBed } from '@angular/core/testing';

import { CardGroupService } from './CardGroupService';

describe('CurrentClimateDataPoint', () => {
  let service: CardGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CardGroupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
