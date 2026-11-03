import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientEspaceComponent } from './client-espace.component';

describe('ClientEspaceComponent', () => {
  let component: ClientEspaceComponent;
  let fixture: ComponentFixture<ClientEspaceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientEspaceComponent]
    });
    fixture = TestBed.createComponent(ClientEspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
