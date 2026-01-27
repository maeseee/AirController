import { TestBed } from '@angular/core/testing';

import { CurrentIndoorClimateDataPointService } from './current-indoor-climate-data-point.service';

describe('CurrentIndoorClimateDataPoint', () => {
  let service: CurrentIndoorClimateDataPointService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentIndoorClimateDataPointService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
