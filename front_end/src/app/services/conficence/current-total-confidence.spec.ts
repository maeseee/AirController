import { TestBed } from '@angular/core/testing';

import { CurrentTotalConfidence } from './current-total-confidence';

describe('CurrentTotalConfidence', () => {
  let service: CurrentTotalConfidence;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentTotalConfidence);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
