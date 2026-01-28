import { TestBed } from '@angular/core/testing';

import { CurrentClimateDataPointService } from './current-climate-data-point.service';

describe('CurrentClimateDataPoint', () => {
  let service: CurrentClimateDataPointService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentClimateDataPointService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
