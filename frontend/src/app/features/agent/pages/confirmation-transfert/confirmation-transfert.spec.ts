import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmationTransfert } from './confirmation-transfert';

describe('ConfirmationTransfert', () => {
  let component: ConfirmationTransfert;
  let fixture: ComponentFixture<ConfirmationTransfert>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationTransfert],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationTransfert);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
