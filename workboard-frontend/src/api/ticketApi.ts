import { apiClient } from "./client";
import type {
  CreateTicketRequest,
  TicketDetailDto,
  TicketListItemDto,
  TicketPageDto,
  UpdateTicketRequest,
} from "@/types/api";

export const ticketApi = {
  getTicketsForProject: async (projectId: number): Promise<TicketListItemDto[]> => {
    const { data } = await apiClient.get<TicketPageDto>(`/tickets/${projectId}/tickets`);
    return data.tickets;
  },

  getTicket: async (ticketId: number): Promise<TicketDetailDto> => {
    const { data } = await apiClient.get<TicketDetailDto>(`/tickets/${ticketId}`);
    return data;
  },

  createTicket: async (
    projectId: number,
    payload: CreateTicketRequest,
  ): Promise<TicketDetailDto> => {
    const { data } = await apiClient.post<TicketDetailDto>(
      `/tickets/${projectId}/new`,
      payload,
    );
    return data;
  },

  updateTicket: async (
    ticketId: number,
    payload: UpdateTicketRequest,
  ): Promise<TicketDetailDto> => {
    const { data } = await apiClient.patch<TicketDetailDto>(
      `/tickets/${ticketId}/update`,
      payload,
    );
    return data;
  },
};
