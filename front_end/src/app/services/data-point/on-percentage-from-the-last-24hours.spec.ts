import { TestBed } from '@angular/core/testing';

import { OnPercentageFromTheLast24Hours } from './on-percentage-from-the-last-24hours';

describe('OnPercentageFromTheLast24Hours', () => {
  let service: OnPercentageFromTheLast24Hours;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OnPercentageFromTheLast24Hours);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
