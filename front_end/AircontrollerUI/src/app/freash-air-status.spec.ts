import { TestBed } from '@angular/core/testing';

import { FreashAirStatus } from './freash-air-status';

describe('FreashAirStatus', () => {
  let service: FreashAirStatus;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FreashAirStatus);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
