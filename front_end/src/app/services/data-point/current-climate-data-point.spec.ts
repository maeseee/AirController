import { TestBed } from '@angular/core/testing';

import { CurrentClimateDataPoint } from './current-climate-data-point';

describe('CurrentClimateDataPoint', () => {
  let service: CurrentClimateDataPoint;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentClimateDataPoint);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
