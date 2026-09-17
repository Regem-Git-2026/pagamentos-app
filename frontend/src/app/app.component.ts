import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { PagamentoComponent } from './pagamento.component';
import { PagamentoListaComponent } from './pagamento-lista.component';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HttpClientModule, PagamentoComponent, PagamentoListaComponent],
  templateUrl: './app.component.html'
})
export class AppComponent {
  toast = inject(ToastService);
  reload = 0;
}
