import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  message: string;
  type: 'ok' | 'err';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private subject = new BehaviorSubject<Toast | null>(null);
  readonly toast$ = this.subject.asObservable();

  show(message: string, type: 'ok' | 'err' = 'ok') {
    this.subject.next({ message, type });
    setTimeout(() => this.subject.next(null), 3500);
  }
}
