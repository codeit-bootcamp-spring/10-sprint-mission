type EventCallback = (args: EventEmitterArgs | undefined) => void;

export interface EventEmitterArgs {
  error: any;
  alert: boolean;
}

class EventEmitter {
  private events: Record<string, EventCallback[]> = {};

  on(event: string, callback: EventCallback): (event: string, callback: EventCallback) => void {
    if (!this.events[event]) {
      this.events[event] = [];
    }
    this.events[event].push(callback);
    return () => this.off(event, callback);
  }

  off(event: string, callback: EventCallback): void {
    if (!this.events[event]) return;
    this.events[event] = this.events[event].filter(cb => cb !== callback);
  }

  emit(event: string, args?: EventEmitterArgs): void {
    if (!this.events[event]) return;
    this.events[event].forEach(callback => {
      callback(args);
    });
  }
}

export const eventEmitter = new EventEmitter(); 