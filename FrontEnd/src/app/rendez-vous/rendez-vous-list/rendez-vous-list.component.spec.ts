import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RendezVousListComponent } from './rendez-vous-list.component';

describe('RendezVousListComponent', () => {
  let component: RendezVousListComponent;
  let fixture: ComponentFixture<RendezVousListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RendezVousListComponent]
    });
    fixture = TestBed.createComponent(RendezVousListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
