import { TestBed } from '@angular/core/testing';

import { GraphViewService } from './GraphViewService';

describe('GraphView', () => {
  let service: GraphViewService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GraphViewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
