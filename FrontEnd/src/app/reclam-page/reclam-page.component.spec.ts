import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReclamPageComponent } from './reclam-page.component';

describe('ReclamPageComponent', () => {
  let component: ReclamPageComponent;
  let fixture: ComponentFixture<ReclamPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReclamPageComponent]
    });
    fixture = TestBed.createComponent(ReclamPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
