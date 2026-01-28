import { TestBed } from '@angular/core/testing';

import { CurrentOutdoorClimateDataPointService } from './current-outdoor-climate-data-point.service';

describe('CurrentOutdoorClimateDataPointService', () => {
  let service: CurrentOutdoorClimateDataPointService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentOutdoorClimateDataPointService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
