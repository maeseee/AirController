import { TestBed } from '@angular/core/testing';

import { FreshAirStatus } from './fresh-air-status.service';

describe('FreashAirStatus', () => {
  let service: FreshAirStatus;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FreshAirStatus);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
