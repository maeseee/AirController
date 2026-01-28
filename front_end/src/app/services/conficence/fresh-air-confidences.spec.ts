import { TestBed } from '@angular/core/testing';

import { FreshAirConfidences } from './fresh-air-confidences';

describe('FreshAirConfidences', () => {
  let service: FreshAirConfidences;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FreshAirConfidences);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
