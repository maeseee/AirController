import { TestBed } from '@angular/core/testing';

import { freshAirStatus } from './fresh-air-status';

describe('FreashAirStatus', () => {
  let service: freshAirStatus;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(freshAirStatus);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
